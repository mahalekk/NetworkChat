package chat.server.authentication;

import chat.server.models.User;

import java.util.List;

public class BaseAuthenticationService implements AuthenticationService {

    private static final List<User> clients = List.of(
            new User("user1", "1111", "Arman"),
            new User("user2", "2222", "Alex"),
            new User("user3", "3333", "Tom"),
            new User("user4", "4444", "Jerry")
    );

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User client : clients) {
            if (client.getLogin().equals(login) && client.getPassword().equals(password)) {
                return client.getUsername();
            }
        }
        return null;
    }

    @Override
    public void startAuthentication() {
        System.out.println("Старт аутентификации");

    }

    @Override
    public void endAuthentication() {
        System.out.println("Конец аутентификации");

    }
}
