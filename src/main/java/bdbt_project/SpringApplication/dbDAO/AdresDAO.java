package bdbt_project.SpringApplication.dbDAO;

import bdbt_project.SpringApplication.dbtables.Adres;
import bdbt_project.SpringApplication.dbtables.Klient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class AdresDAO {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public AdresDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Adres> list(){
        String sql = "SELECT * FROM ADRESY";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Adres.class));
    }

    public Adres getHighestIdAddress() {
        String sql = "SELECT nr_adresu FROM ADRESY ORDER BY nr_adresu DESC FETCH NEXT 1 ROWS ONLY";
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Adres.class));
    }

    public void save(Adres adres) {
        var insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName("ADRESY")
                .usingColumns("nr_adresu", "kraj", "miasto",
                        "kod_pocztowy", "ulica", "nr_domu",
                        "nr_mieszkania");
        var param = new BeanPropertySqlParameterSource(adres);
        insert.execute(param);
    }

    public List<Adres> listAdresyForSchroniskaId(List<Integer> ids) {
        var lista = new ArrayList<Adres>();
        for(var id: ids) {
            String sql = "SELECT * FROM ADRESY WHERE nr_adresu='"+id+"'";
            lista.add(jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Adres.class)));
        }
        return lista;
    }

    public Adres get(int nr_adresu) {
        var sql = "SELECT * FROM ADRESY WHERE NR_KLIENTA='"+nr_adresu+"'";
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Adres.class));
    }

    public void update(Adres adres) {
    }

    public void delete(int nr_adresu) {
    }

}
