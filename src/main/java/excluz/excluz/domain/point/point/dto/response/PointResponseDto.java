package excluz.excluz.domain.point.point.dto.response;
import excluz.excluz.common.entity.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointResponseDto {
    private Integer amount;

    public PointResponseDto(Integer amount) {
        this.amount = amount;
    }

    public static PointResponseDto from(Point point) {
        return new PointResponseDto(point.getAmount());
    }


}
