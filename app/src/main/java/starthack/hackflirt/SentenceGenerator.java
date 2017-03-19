package starthack.hackflirt;

import java.util.ArrayList;
import java.util.List;

public class SentenceGenerator {

    public static List<String> generateSentences(
            User user) {
        List<String> sentences = new ArrayList<>();
        sentences.add("My friends know me as " + user.getName());
        sentences.add("My surname is " + user.getSurname());
        sentences.add("I am " + user.getAge() + " years old");
        sentences.add("In fact I prefer " + user.getPreference() + " gender");
        sentences.add("As you can hear I am a " + user.getGender());
        return sentences;
    }
}
