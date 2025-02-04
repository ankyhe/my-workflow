package com.gmail.at.ankyhe.wdl.parser.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class ArrayType implements Type {

    @NotNull
    private Type elementType;

    @Override
    public String toString() {
        return "Array[%s]".formatted(this.elementType);
    }
}
