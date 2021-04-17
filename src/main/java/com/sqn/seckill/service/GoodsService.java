package com.sqn.seckill.service;

import com.sqn.seckill.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sqn.seckill.vo.GoodsVO;

import java.util.List;

/**
 * <p>
 * 秒杀-商品 服务类
 * </p>
 *
 * @author sqn
 * @since 2021-04-15
 */
public interface GoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     *
     * @return
     */
    List<GoodsVO> findGoodsVO();

    /**
     * 获取商品详情
     *
     * @return
     */
    GoodsVO findGoodsVOByGoodsId(Long goodsId);
}
