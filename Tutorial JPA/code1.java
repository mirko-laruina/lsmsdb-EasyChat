@Entity
@Table(name = "Messages")
public class Message implements Comparable<Message> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "senderUserId")
    private User sender;

    @Column(name = "timestamp")
    private Timestamp sqlTimestamp;

    @Transient
    private Instant instantTimestamp;

    @Column(name = "text")
    private String text;