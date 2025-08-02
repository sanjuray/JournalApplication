package net.engineeringdigest.journalApp.repository;

import net.engineeringdigest.journalApp.entity.JournalConfigEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalConfigRepository extends MongoRepository<JournalConfigEntity, ObjectId> {
}