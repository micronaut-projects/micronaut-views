package com.projectcheckins.repositories.jdbc;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JdbcRepository(dialect = Dialect.H2)
public interface QuestionUserRepository extends CrudRepository<QuestionUser, QuestionUserId> {
    @Query("select id_user_id from question_user where id_question_id = :questionId")
    List<Long> findAllUserIdByQuestionId(@NonNull @NotNull Long questionId);

    @Query("select count(*) from question_user where id_question_id = :questionId")
    long countByQuestionId(@NonNull @NotNull Long questionId);

    @Query("delete from question_user where id_question_id = :questionId")
    void deleteByQuestionId(Long questionId);
}
