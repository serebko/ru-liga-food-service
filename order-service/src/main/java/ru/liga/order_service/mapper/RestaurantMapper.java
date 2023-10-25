package ru.liga.order_service.mapper;

import entities.RestaurantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RestaurantMapper {
    @Select(value = "select * from restaurant where id = #{id}")
    RestaurantEntity findById(@Param("id") Long id);

    @Select(value = "select * from restaurant where name = #{name}")
    RestaurantEntity findByName(@Param("name") String name);
}
