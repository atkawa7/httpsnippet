package io.github.atkawa7.httpsnippet.models.internal;

import java.util.ArrayList;
import java.util.List;

import io.github.atkawa7.httpsnippet.utils.ObjectUtils;

public interface Validation<T> {
  static <T> List<T> validate(List<T> validationList, Validation<T> validation) throws Exception {
    List<T> result = new ArrayList<>();
    if (ObjectUtils.isNotNull(validationList) && ObjectUtils.isNotNull(validation)) {
      for (T obj : validationList) {
        if (ObjectUtils.isNull(obj)) {
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
