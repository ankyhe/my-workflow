package com.gmail.at.ankyhe.wdl.parser.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnyType extends AbstractBaseType {

    public static final Type TYPE = new AnyType();
}
