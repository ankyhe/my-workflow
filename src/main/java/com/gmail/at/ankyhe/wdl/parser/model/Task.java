package com.gmail.at.ankyhe.wdl.parser.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class Task {

    @NotBlank
    private String name;

    private Input input;

    private Output output;

    @AssertTrue
    public boolean isValid() {
        if (this.output == null) {
            return true;
        }

        return this.output.getDeclarations().stream().noneMatch(d -> d instanceof BoundDeclaration);
    }

    @Override
    public String toString() {
        return "Task: %s %s %s".formatted(this.name, this.input, this.output);
    }
}
