@Entity
@Table(name = "Chats")
public class Chat implements Comparable<Chat> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chatId;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(
            name = "Chatmembers",
            joinColumns = @JoinColumn(name = "chatId"),
            inverseJoinColumns = @JoinColumn(name = "userId")
    )
    private List<User> members = new ArrayList<>();