package io.github.atkawa7.httpsnippet.demo.dto.converters;

public interface DTOConverter<E, D> {
  E toEntity(D dto);

  D toDTO(E en);
}
