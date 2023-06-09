package bdbt_project.SpringApplication.dbDAO;

import bdbt_project.SpringApplication.dbtables.Adres;
import bdbt_project.SpringApplication.dbtables.Klient;
import bdbt_project.SpringApplication.dbtables.Umowa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class KlientDAO {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public KlientDAO(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Klient> list(){
        String sql = "SELECT * FROM KLIENCI";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Klient.class));
    }

    public Klient getByNrKlienta(int nr_klienta) {
        var sql = "SELECT * FROM KLIENCI WHERE NR_KLIENTA='"+nr_klienta+"'";
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Klient.class));
    }

    public Klient getByEmail(String email) {
        System.out.println(email);
        var sql = "SELECT * FROM KLIENCI WHERE EMAIL='"+email+"' ORDER BY nr_klienta DESC";
        var found = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Klient.class));
        if(found.size() == 0) {
            throw new RuntimeException("Nie znaleziono użytkownika");
        }
        return found.get(0);
    }

    public void save(Klient k) {
        var insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName("klienci").usingColumns("nr_klienta", "imie", "nazwisko", "data_urodzenia", "pesel", "plec", "email", "nr_telefonu", "nr_adresu", "nr_schroniska");
        var param = new BeanPropertySqlParameterSource(k);
        insert.execute(param);
    }

    public List<Klient> getKlienciByUmowy(List<Umowa> umowy) {
        var klienci = new ArrayList<Klient>();
        for(var umowa: umowy) {
            klienci.add(getByNrKlienta(umowa.getNr_klienta()));
        }
        return klienci;
    }

    public void update(Klient k) {
        String sql = "UPDATE KLIENCI SET imie='"+k.getImie()+"', nazwisko='"+k.getNazwisko()+"', " +
                "data_urodzenia='"+ k.getData_urodzenia_formatted()+"', pesel='"+k.getPesel()+"', " +
                "plec='"+k.getPlec()+"', email='"+k.getEmail()+"', " +
                "nr_telefonu='"+k.getNr_telefonu()+"' " +
                "WHERE nr_klienta='"+k.getNr_klienta()+"'";
        System.out.println(sql);
        jdbcTemplate.update(sql);
    }

    public Klient updateOnlyNotNull(Klient current, Klient newA) {
        System.out.println(newA);
        if(newA.getImie() != null && !Objects.equals(newA.getImie(), "")) current.setImie(newA.getImie());
        if(newA.getNazwisko() != null && !Objects.equals(newA.getNazwisko(), "")) current.setNazwisko(newA.getNazwisko());
        if(newA.getData_urodzenia() != null && !Objects.equals(newA.getData_urodzenia(), "")) current.setData_urodzenia(newA.getData_urodzenia());
        if(newA.getPesel() != null && !Objects.equals(newA.getPesel(), "")) current.setPesel(newA.getPesel());
        if(newA.getEmail() != null && !Objects.equals(newA.getEmail(), "")) current.setEmail(newA.getEmail());
        if(newA.getNr_telefonu() != null && !Objects.equals(newA.getNr_telefonu(), "")) current.setNr_telefonu(newA.getNr_telefonu());
        return current;
    }
}
