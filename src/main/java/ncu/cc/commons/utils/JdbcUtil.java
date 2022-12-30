package ncu.cc.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import javax.persistence.NamedNativeQuery;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0.1
 * @since 1.0
 */
public class JdbcUtil<T> {
    @FunctionalInterface
    public interface ResultSetHandler<T> {
        @Nullable
        void acceptData(T result) throws SQLException, DataAccessException;
    }

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    private final Class<T> tClass;
    private final RowMapper<T> rowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String,String> namedQueryMap;

    public JdbcUtil(Class<T> tClass, JdbcTemplate jdbcTemplate) {
        this(tClass, jdbcTemplate, new BeanPropertyRowMapper<>(tClass));
    }

    public JdbcUtil(Class<T> tClass, JdbcTemplate jdbcTemplate, RowMapper<T> rowMapper) {
        this.tClass = tClass;
        this.rowMapper = rowMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.namedQueryMap = new HashMap<>();

        for (NamedNativeQuery namedQuery: tClass.getAnnotationsByType(NamedNativeQuery.class)) {
            this.namedQueryMap.put(namedQuery.name(), namedQuery.query());
        }
    }

    public void namedNativeQuery(String name, ResultSetHandler<T> resultSetHandler) throws DataAccessException {
        if (namedQueryMap.containsKey(name)) {
            query(namedQueryMap.get(name), resultSetHandler);
        } else {
            logger.error("NamedNativeQuery {} not found", name);
        }
    }

    public void namedNativeQuery(String name, Object[] args, ResultSetHandler<T> resultSetHandler) throws DataAccessException {
        if (namedQueryMap.containsKey(name)) {
            query(namedQueryMap.get(name), args, resultSetHandler);
        } else {
            logger.error("NamedNativeQuery {} not found", name);
        }
    }

    public void query(String sql, ResultSetHandler<T> resultSetHandler) throws DataAccessException {
        jdbcTemplate.query(sql, resultSet -> {
            resultSetHandler.acceptData(this.rowMapper.mapRow(resultSet, 0));
        });
    }

    public void query(String sql, Object[] args, ResultSetHandler<T> resultSetHandler) throws DataAccessException {
        jdbcTemplate.query(sql, args, resultSet -> {
            resultSetHandler.acceptData(this.rowMapper.mapRow(resultSet, 0));
        });
    }
}
