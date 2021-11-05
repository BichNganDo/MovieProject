package model;

import client.MysqlClient;
import common.ErrorCode;
import entity.film.Film;
import entity.film.FilterFilm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class FilmModel {

    private static final MysqlClient dbClient = MysqlClient.getMysqlCli();
    private final String NAMETABLE = "film";
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    public static FilmModel INSTANCE = new FilmModel();

    public List<Film> getSliceFilm(FilterFilm filterFilm) {
        List<Film> resultListFilm = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return resultListFilm;

            }
            String sql = "SELECT film.*, cate_film.name AS `category` "
                    + "FROM film "
                    + "INNER JOIN cate_film ON film.id_cate= cate_film.id "
                    + "WHERE 1=1";

            if (StringUtils.isNotEmpty(filterFilm.getSearchQuery())) {
                sql = sql + " AND film.title LIKE ? ";
            }

            if (filterFilm.getSearchCate() > 0) {
                sql = sql + " AND film.id_cate = ? ";
            }
            if (filterFilm.getSearchStatus() > 0) {
                sql = sql + " AND film.status = ? ";
            }
            if (filterFilm.getSearchProperty() > 0) {
                sql = sql + " AND film.property = ? ";
            }

            sql = sql + " LIMIT ? OFFSET ? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterFilm.getSearchQuery())) {
                ps.setString(param++, "%" + filterFilm.getSearchQuery() + "%");
            }

            if (filterFilm.getSearchCate() > 0) {
                ps.setInt(param++, filterFilm.getSearchCate());
            }
            if (filterFilm.getSearchStatus() > 0) {
                ps.setInt(param++, filterFilm.getSearchStatus());
            }

            if (filterFilm.getSearchProperty() > 0) {
                ps.setInt(param++, filterFilm.getSearchProperty());
            }

            ps.setInt(param++, filterFilm.getLimit());
            ps.setInt(param++, filterFilm.getOffset());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Film film = new Film();
                film.setId(rs.getInt("id"));
                film.setIdCate(rs.getInt("id_cate"));
                film.setCategory(rs.getString("category"));
                film.setTitle(rs.getString("title"));
                film.setDescription(rs.getString("description"));
                film.setContent(rs.getString("content"));
                film.setPosterUrlWithBaseDomain(rs.getString("poster"));
                film.setDuration(rs.getString("duration"));
                film.setOpenDay(rs.getString("opening_day"));
                film.setTrailer(rs.getString("trailer"));
                film.setStatus(rs.getInt("status"));
                film.setProperty(rs.getInt("property"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateString = sdf.format(date);
                film.setCreatedDate(dateString);

                resultListFilm.add(film);
            }

            return resultListFilm;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return resultListFilm;
    }

    public int getTotalProduct(FilterFilm filterFilm) {
        int total = 0;
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return total;
            }

            String sql = "SELECT COUNT(id) AS total FROM `" + NAMETABLE + "` WHERE 1 = 1";
            if (StringUtils.isNotEmpty(filterFilm.getSearchQuery())) {
                sql = sql + " AND film.title LIKE ? ";
            }

            if (filterFilm.getSearchCate() > 0) {
                sql = sql + " AND film.id_cate = ? ";
            }

            if (filterFilm.getSearchProperty() > 0) {
                sql = sql + " AND film.property = ? ";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            int param = 1;

            if (StringUtils.isNotEmpty(filterFilm.getSearchQuery())) {
                ps.setString(param++, "%" + filterFilm.getSearchQuery() + "%");
            }

            if (filterFilm.getSearchCate() > 0) {
                ps.setInt(param++, filterFilm.getSearchCate());
            }

            if (filterFilm.getSearchProperty() > 0) {
                ps.setInt(param++, filterFilm.getSearchProperty());
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return total;
    }

    public Film getFilmByID(int id) {
        Film result = new Film();
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return result;
            }
            PreparedStatement getProductByIdStmt = conn.prepareStatement("SELECT * FROM `" + NAMETABLE + "` WHERE film.id = ? ");
            getProductByIdStmt.setInt(1, id);

            ResultSet rs = getProductByIdStmt.executeQuery();

            if (rs.next()) {
                result.setId(rs.getInt("id"));
                result.setIdCate(rs.getInt("id_cate"));
                result.setTitle(rs.getString("title"));
                result.setPosterUrlWithBaseDomain(rs.getString("poster"));
                result.setDescription(rs.getString("description"));
                result.setContent(rs.getString("content"));
                result.setDuration(rs.getString("duration"));
                result.setOpenDay(rs.getString("opening_day"));
                result.setTrailer(rs.getString("trailer"));
                result.setProperty(rs.getInt("property"));
                result.setStatus(rs.getInt("status"));

                long currentTimeMillis = rs.getLong("created_date");
                Date date = new Date(currentTimeMillis);
                String dateString = sdf.format(date);
                result.setCreatedDate(dateString);
            }

            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return result;
    }

    public int addFilm(Film film) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            PreparedStatement addStmt = conn.prepareStatement("INSERT INTO `" + NAMETABLE + "` (id_cate, title, poster, description, "
                    + "content, duration, opening_day, trailer, status, property, created_date) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            addStmt.setInt(1, film.getIdCate());
            addStmt.setString(2, film.getTitle());
            addStmt.setString(3, film.getPoster());
            addStmt.setString(4, film.getDescription());
            addStmt.setString(5, film.getContent());
            addStmt.setString(6, film.getDuration());
            addStmt.setString(7, film.getOpenDay());
            addStmt.setString(8, film.getTrailer());
            addStmt.setInt(9, film.getStatus());
            addStmt.setInt(10, film.getProperty().getValue());
            addStmt.setString(11, System.currentTimeMillis() + "");
            int rs = addStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }

        return ErrorCode.FAIL.getValue();
    }

    public int editFilm(Film film) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            PreparedStatement editStmt = conn.prepareStatement("UPDATE `" + NAMETABLE + "` SET id_cate = ?, title = ?, poster = ?, "
                    + "description = ?, content = ?, duration = ?, opening_day = ?, trailer = ?, status = ?, property = ? WHERE id = ? ");
            editStmt.setInt(1, film.getIdCate());
            editStmt.setString(2, film.getTitle());
            editStmt.setString(3, film.getPoster());
            editStmt.setString(4, film.getDescription());
            editStmt.setString(5, film.getContent());
            editStmt.setString(6, film.getDuration());
            editStmt.setString(7, film.getOpenDay());
            editStmt.setString(8, film.getTrailer());
            editStmt.setInt(9, film.getStatus());
            editStmt.setInt(10, film.getProperty().getValue());
            editStmt.setInt(11, film.getId());
            int rs = editStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

    public int deleteFilm(int id) {
        Connection conn = null;
        try {
            conn = dbClient.getDbConnection();
            if (null == conn) {
                return ErrorCode.CONNECTION_FAIL.getValue();
            }
            Film filmById = getFilmByID(id);
            if (filmById.getId() == 0) {
                return ErrorCode.NOT_EXIST.getValue();
            }
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM `" + NAMETABLE + "` WHERE id = ?");
            deleteStmt.setInt(1, id);
            int rs = deleteStmt.executeUpdate();

            return rs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            dbClient.releaseDbConnection(conn);
        }
        return ErrorCode.FAIL.getValue();
    }

}
