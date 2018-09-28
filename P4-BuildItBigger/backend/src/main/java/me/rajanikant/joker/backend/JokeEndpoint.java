package me.rajanikant.joker.backend;

import com.example.Joker;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

@Api(
  name = "jokeApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.joker.rajanikant.me",
    ownerName = "backend.joker.rajanikant.me"
  )
)
public class JokeEndpoint {

    @ApiMethod(name = "tellJoke")
    public Joke tellJoke() {
        Joke response = new Joke();
        response.setContents(Joker.getJoke());
        return response;
    }

}
