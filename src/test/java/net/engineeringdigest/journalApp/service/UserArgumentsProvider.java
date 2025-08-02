package net.engineeringdigest.journalApp.service;

import com.sun.org.apache.xpath.internal.Arg;
import net.engineeringdigest.journalApp.entity.UserEntity;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class UserArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(UserEntity.builder().userName("shyam").password("").build()),
                Arguments.of(UserEntity.builder().userName("ram").password("ram").build())
        );
    }
}
