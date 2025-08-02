package net.aldisti.common.finance;

import lombok.*;
import net.aldisti.common.fix.constants.Instrument;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Asset {
    private String id;
    private String name;
    private Instrument instrument;
    private Integer quantity;
    private Integer price;
}
