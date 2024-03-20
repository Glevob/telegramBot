package ru.telgram.jokebot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.telgram.jokebot.model.Joke;

public interface JokesRepository extends JpaRepository<Joke, Long> {
}
