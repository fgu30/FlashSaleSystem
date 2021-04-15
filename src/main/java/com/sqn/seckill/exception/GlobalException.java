package com.sqn.seckill.exception;

import com.sqn.seckill.vo.RespBean;
import com.sqn.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Title: GlobalException
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/13 0013 下午 12:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {
   private RespBeanEnum respBeanEnum;
}
