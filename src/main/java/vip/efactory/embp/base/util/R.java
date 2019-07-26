package vip.efactory.embp.base.util;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author admin
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int code = 0; //成功

    @Getter
    @Setter
    private String msg = "success";


    @Getter
    @Setter
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, 0, null);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 0, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 0, msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, 1, null);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, 1, msg);
    }

    public static <T> R<T> failed(T data) {
        return restResult(data, 1, null);
    }

    public static <T> R<T> failed(T data, String msg) {
        return restResult(data, 1, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
