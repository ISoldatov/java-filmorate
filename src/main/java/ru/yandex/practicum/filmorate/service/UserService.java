package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationUtil;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        return storage.save(user);
    }

    public User update(User user) {
        return ValidationUtil.checkNotFoundWithId(storage.save(user), user.getId());
    }

    public void delete(int id) {
        ValidationUtil.checkNotFoundWithId(storage.delete(id), id);
    }

    public User get(int id) {
        return ValidationUtil.checkNotFoundWithId(storage.get(id), id);
    }

    public List<User> getAll() {
        return storage.getAll();
    }

    public void addFriend(int id, int friendId) {
        User user = ValidationUtil.checkNotFoundWithId(storage.get(id), id);
        User friendUser = ValidationUtil.checkNotFoundWithId(storage.get(friendId), friendId);
//        storage.get(id).getFriends().add(friendUser.getId());
        user.getFriends().add(friendId);
        friendUser.getFriends().add(id);
    }

    public void removeFriend(int id, int friendId) {
        User user = ValidationUtil.checkNotFoundWithId(storage.get(id), id);
        User friendUser = ValidationUtil.checkNotFoundWithId(storage.get(friendId), friendId);
        user.getFriends().remove(friendId);
        friendUser.getFriends().remove(id);
    }

    public List<User> getFriends(int id) {
        return storage.get(id).getFriends().stream()
                .map(storage::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommFriends(int id, int otherId) {
        List<Integer> allFriendsBothUsers = Stream.of(storage.get(id).getFriends(), storage.get(otherId).getFriends())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return allFriendsBothUsers.stream()
                .filter((i -> Collections.frequency(allFriendsBothUsers, i) > 1))
                .map(storage::get)
                .distinct()
                .collect(Collectors.toList());
    }
}
