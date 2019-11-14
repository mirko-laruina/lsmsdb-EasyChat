@Entity
@Table(name = "Chats")
public class Chat implements Comparable<Chat> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long chatId;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "chat", cascade = { CascadeType.ALL } )
    private List<Message> messages = new ArrayList<>();