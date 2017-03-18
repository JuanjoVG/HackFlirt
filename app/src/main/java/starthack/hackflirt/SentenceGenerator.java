package starthack.hackflirt;

import java.util.HashSet;
import java.util.Set;

public class SentenceGenerator {

    public static Set<String> generateSentences(
            User user) {
        HashSet<String> sentences = new HashSet<>();
        sentences.add("My friends know me as aaaaaaa hhh sadij isujhdaks askj" + user.getName());
        sentences.add("My surname is " + user.getSurname());
        sentences.add("I am " + user.getAge() + " old");
        sentences.add("In fact I prefer " + user.getPreference() + " gender");
        sentences.add("As you can hear I am a " + user.getGender());
        return sentences;
    }
}
