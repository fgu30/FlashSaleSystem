package com.sqn.seckill.rabbitmq;

import com.sqn.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Title: SeckillMessage
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/27 0027 下午 3:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessage implements Serializable {

    private static final long serialVersionUID = 8763696299123564381L;

    private User user;
    private Long goodsId;

}
