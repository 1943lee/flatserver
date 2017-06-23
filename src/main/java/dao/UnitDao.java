package dao;

import model.Unit;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by tanhuizhen on 2016/12/7.
 */

@Repository
public interface UnitDao {
    public Unit getUnitByDwbh(Unit unit);

    public int insertUnit(Unit unit);

    public List<Map<String, String>> getUnit();

    public int updateUnit(Unit unit);

    public int deleteUnit(Unit unit);

    public List<Map<String, String>> getByNotIds(List<String> ids);

    public Map<String, String> getDwmcByDwbh(String dwbh);

}
