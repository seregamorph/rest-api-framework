package com.seregamorph.restapi.demo.services;

import static com.seregamorph.restapi.demo.utils.ResourceFactory.MAX_USER_ID;
import static com.seregamorph.restapi.demo.utils.ResourceFactory.MIN_USER_ID;

import com.seregamorph.restapi.demo.resources.UserResource;
import com.seregamorph.restapi.demo.utils.ResourceFactory;
import com.seregamorph.restapi.sort.Sort;
import com.seregamorph.restapi.sort.SortDirection;
import com.seregamorph.restapi.sort.SortField;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SortService {

    public List<UserResource> sort(Sort sort) {
        List<UserResource> users = new ArrayList<>();
        for (long i = MIN_USER_ID; i <= MAX_USER_ID; ++i) {
            users.add(ResourceFactory.user(i));
        }
        users.sort(toComparator(sort));
        return users;
    }

    private static Comparator<UserResource> toComparator(Sort sort) {
        List<Comparator<UserResource>> comparators = sort.stream()
                .map(SortService::toComparator)
                .collect(Collectors.toList());
        Comparator<UserResource> comparator = comparators.get(0);
        for (int i = 1; i < comparators.size(); ++i) {
            comparator = comparator.thenComparing(comparators.get(i));
        }
        return comparator;
    }

    private static Comparator<UserResource> toComparator(SortField sortField) {
        Comparator<UserResource> comparator = Comparator.comparing(UserResource::getId);

        if (UserResource.Fields.NAME.equals(sortField.getFieldName())) {
            comparator = Comparator.comparing(UserResource::getName);
        } else if (UserResource.Fields.AGE.equals(sortField.getFieldName())) {
            comparator = Comparator.comparing(UserResource::getAge);
        } else if (UserResource.Fields.ADDRESS.equals(sortField.getFieldName())) {
            comparator = Comparator.comparing(UserResource::getAddress);
        } else if (UserResource.Fields.STATUS.equals(sortField.getFieldName())) {
            comparator = Comparator.comparing(UserResource::getStatus);
        } else if (UserResource.Fields.GROUP_ID.equals(sortField.getFieldName())) {
            comparator = Comparator.comparing(user -> user.getGroup().getId());
        } else if (UserResource.Fields.GROUP_NAME.equals(sortField.getFieldName())) {
            comparator = Comparator.comparing(user -> user.getGroup().getName());
        } else if (UserResource.Fields.GROUP_DESC.equals(sortField.getFieldName())) {
            comparator = Comparator.comparing(user -> user.getGroup().getDesc());
        }

        return sortField.getDirection() == SortDirection.ASC ? comparator : comparator.reversed();
    }
}
