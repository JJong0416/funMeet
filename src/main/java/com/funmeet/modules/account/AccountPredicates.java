package com.funmeet.modules.account;

import com.funmeet.modules.city.City;
import com.funmeet.modules.hobby.Hobby;
import com.querydsl.core.types.Predicate;

import java.util.List;


public class AccountPredicates {

    public static Predicate findByHobbyAndCity(List<Hobby> hobbies, List<City> cities) {
        QAccount account = QAccount.account;
        return account.city.any().in(cities).and(account.hobby.any().in(hobbies));
    }
}
