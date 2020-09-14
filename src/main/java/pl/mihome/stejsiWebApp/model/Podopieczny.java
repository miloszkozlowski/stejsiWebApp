package pl.mihome.stejsiWebApp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.stempel.StempelPolishStemFilterFactory;
import org.hibernate.annotations.BatchSize;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.elasticsearch.analyzer.ElasticsearchTokenFilterFactory;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "uzytkownicy")
@Indexed
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Podopieczny.class)
@AnalyzerDef(name = "polishanal",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = StempelPolishStemFilterFactory.class),
                @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class)
        })
public class Podopieczny extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pole imię nie może być puste")
    @Size(min = 3, max = 30)
    @Field(analyzer = @Analyzer(definition = "polishanal"))
    private String imie;

    @NotEmpty(message = "Pole nazwisko nie może być puste")
    @Size(min = 3, max = 50)
    @Field(analyzer = @Analyzer(definition = "polishanal"))
    private String nazwisko;

    @NotEmpty(message = "Pole email nie może być puste")
    @Email(message = "Pole e-email musi zawierać prawidłowy adres e-mail")
    @Size(max = 200)
    @Field(analyze = Analyze.NO)
    private String email;

    @Field
    private int phoneNumber;

    private boolean aktywny;

    private boolean settingTipNotifications;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private Set<PakietTreningow> trainingPackages;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private Set<TipComment> comments;

    @ManyToMany
    @JoinTable(name = "tip_read_status",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tip_id"))
    private Set<Tip> tipsRead;

    @OneToMany(mappedBy = "owner")
    private Set<Token> tokens;


    public Podopieczny() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAktywny() {
        return aktywny;
    }

    public void setAktywny(boolean aktywny) {
        this.aktywny = aktywny;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<PakietTreningow> getTrainingPackages() {
        return trainingPackages;
    }

    public void setTrainingPackages(Set<PakietTreningow> trainingPackages) {
        this.trainingPackages = trainingPackages;
    }

    public Set<TipComment> getComments() {
        return comments;
    }

    public Set<Tip> getTipsRead() {
        return tipsRead;
    }

    public Set<Token> getTokens() {
        return tokens;
    }

    public void setTokens(Set<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean isSettingTipNotifications() {
        return settingTipNotifications;
    }


    public void setSettingTipNotifications(boolean settingTipNotifications) {
        this.settingTipNotifications = settingTipNotifications;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Podopieczny) {
            var p = (Podopieczny) obj;
			return this.id == p.getId();
		}
        return false;
    }

}
