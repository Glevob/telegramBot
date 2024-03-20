package ru.telgram.jokebot.service;

import ru.telgram.jokebot.model.Joke;

import java.util.List;
import java.util.Optional;

public interface JokeService {
    void registerJoke(Joke joke);
    List<Joke> getAllJokes();
    Optional <Joke> getJokeById(Long id);
    boolean deleteJokeById(Long id);
    Joke getRandomJoke();

}
