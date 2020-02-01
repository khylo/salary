package com.khylo.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple2<T1, T2> {
  protected T1 t1;
  protected T2 t2;
}
