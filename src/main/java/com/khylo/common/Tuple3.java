package com.khylo.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tuple3<T1, T2, T3>{
	protected T1 t1;
	protected T2 t2;
	protected T3 t3;

}
