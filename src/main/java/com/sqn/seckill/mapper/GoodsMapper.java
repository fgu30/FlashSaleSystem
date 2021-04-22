package com.sqn.seckill.mapper;

import com.sqn.seckill.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqn.seckill.vo.GoodsVO;

import java.util.List;

/**
 * <p>
 * 秒杀-商品 Mapper 接口
 * </p>
 *
 * @author sqn
 * @since 2021-04-15
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     *
     * @return
     */
    List<GoodsVO> findGoodsVO();

    /**
     * 获取商品详情
     *
     * @param goodsId
     * @return
     */
    GoodsVO findGoodsVoByGoodsId(Long goodsId);
}
