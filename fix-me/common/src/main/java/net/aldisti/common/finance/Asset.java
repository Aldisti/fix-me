package net.aldisti.common.finance;

import lombok.*;
import net.aldisti.common.fix.constants.Instruments;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Asset {
    private String id;
    private String name;
    private Instruments instrument;
    private Integer quantity;
    private Integer price;
}
