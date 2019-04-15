package io.github.atkawa7.httpsnippet.models.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

public interface Validation<T> {
  static <T> List<T> validate(List<T> validationList, Validation<T> validation) throws Exception {
    List<T> result = new ArrayList<>();
    if (ObjectUtils.isNotEmpty(validationList) && Objects.nonNull(validation)) {
      for (T obj : validationList) {
        if (Objects.isNull(obj)) {
          throw new Exception("object cannot be null");
        }
        validation.validate(obj);
        result.add(obj);
      }
    }
    return result;
  }

  void validate(T obj) throws Exception;
}
