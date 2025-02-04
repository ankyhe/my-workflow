package com.gmail.at.ankyhe.wdl.parser.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class WdlDocument {

    private static final String DEFAULT_VERSION = "1.0";

    private String version;

    @Builder.Default
    private List<@NotNull Task> tasks = new ArrayList<>();

    @Builder.Default
    @Size(max = 1)/* support more in the future */
    private List<@NotNull Workflow> workflows = new ArrayList<>();

    public String getVersion() {
        if (StringUtils.isNotBlank(this.version)) {
            return this.version;
        }

        return DEFAULT_VERSION;
    }
}
