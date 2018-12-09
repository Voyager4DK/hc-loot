package dk.loej.hc.loot.repository;

import org.springframework.data.repository.CrudRepository;

import dk.loej.hc.loot.entity.Comment;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

}
