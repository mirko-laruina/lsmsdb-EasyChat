@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @ManyToMany(mappedBy = "members")
    private List<Chat> chats;