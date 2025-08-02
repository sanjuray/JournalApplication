package net.engineeringdigest.journalApp.controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.service.UserDetailsServiceImpl;
import net.engineeringdigest.journalApp.service.UserService;
import net.engineeringdigest.journalApp.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * This is called by the front-end after, the authentication from 'Google' is completed.
     * @param code
     * @return
     */
    @GetMapping("/callback")
    public ResponseEntity<?> handleCallBack(@RequestParam String code){
        try{
            // 1. Exchange Auth Code for token
//            String tokenEndPoint = "https://oauth2.googleapis.com/token";
//            // Dont use usual Map -> the key-value pairs will be affected in header coz of APPLICATION_FORM_URLENCODE
//
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//
//            params.add("code", code);
//            params.add("client_id", clientId);
//            params.add("client_secret", clientSecret);
//            // ***** NOTE: ensure this uri is same as you have mentioned in the web client of OAuth *****
//            params.add("redirect_uri","https://developers.google.com/oauthplayground");
//            params.add("grant_type","authorization_code");
//
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", "https://developers.google.com/oauthplayground");
            params.add("grant_type", "authorization_code");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            System.out.println("the request:"+request.getBody());
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

//            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndPoint, request, Map.class);

            System.out.println("the tokenResponse: "+tokenResponse);

            String idToken = (String) tokenResponse.getBody().get("id_token");
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token="+idToken;

            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            System.out.println("the userInfoResponse: "+userInfoResponse);

            if(userInfoResponse.getStatusCode() == HttpStatus.OK){
                System.out.println("in OK block");
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                    System.out.println("in userDetails: "+userDetails);
                }catch (Exception e){
                    System.out.println("into saving the new user");
                    UserEntity user = new UserEntity();
                    user.setUserName(email);
                    user.setEmail(email);
                    user.setPassword(UUID.randomUUID().toString());
                    userService.saveNewUser(user);
                    userDetails = userDetailsService.loadUserByUsername(email);
                    System.out.println("the userdetails agin:"+userDetails);
                }

//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                String jwtToken = jwtUtil.generateToken(email);

                return ResponseEntity.ok(Collections.singletonMap("token",jwtToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            log.error("Error in handleCalLBack() of GoogleAuthController"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
