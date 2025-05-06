package com.emobile.springtodo.mapper;

import java.util.List;

public interface Mappable<E, D> {
    E mapToEntity(D dto);

    D mapToDTO(E entity);

    List<E> mapToEntity(List<D> dto);

    List<D> mapToDTO(List<E> entity);
}
