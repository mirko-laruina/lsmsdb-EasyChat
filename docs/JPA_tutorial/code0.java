@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id; 
    //...
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Address address;
    //... getters and setters
}

@Entity
@Table(name = "address")
public class Address {
    @Id
    @Column(name = "id")
    private Long id;
    //...
    @OneToOne
    @MapsId
    private User user;    
    //... getters and setters
}