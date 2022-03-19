import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private final int capacity;
    private int size;
    private final LRUCacheLinkedList list;
    private final Map<K, Node> hashTable;

    public K getHeadKey() {
        return list.getHead().key;
    }

    public V getHeadValue() {
        return list.getHead().value;
    }

    private void assertSize() {
        assert size <= capacity;
    }

    public int getSize() {
        assertSize();
        return size;
    }

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.list = new LRUCacheLinkedList();
        this.hashTable = new HashMap<>();
    }

    public V get(K key) {
        if (!hashTable.containsKey(key)) {
            return null;
        }
        V value = hashTable.get(key).value;
        list.replaceToHead(hashTable.get(key));
        assert list.head.value == value;
        return value;
    }

    public void put(K key, V value) {
        if (hashTable.containsKey(key)) {
            hashTable.get(key).value = value;
            list.replaceToHead(hashTable.get(key));
            assert list.head.value == value && list.head.key == key;
            return;
        }
        assertSize();
        if (size == capacity) {
            K k = list.getTail().key;
            hashTable.remove(k);
            list.eraseFromTail();
            size--;
        }
        assertSize();
        Node newNode = list.addToHead(key, value);
        size++;
        hashTable.put(key, newNode);
        assertSize();
        assert list.head.value == value && list.head.key == key;
    }

    private class Node {
        public Node next;
        public Node prev;
        public K key;
        public V value;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private class LRUCacheLinkedList {
        private Node head;
        private Node tail;

        boolean isEmpty() {
            return tail == null;
        }

        void eraseFromTail() {
            if (isEmpty()) {
                return;
            }
            if (head == tail) {
                head = null;
                tail = null;
                assert isEmpty();
            } else {
                tail = tail.prev;
                tail.next = null;
            }
        }

        Node getTail() {
            return tail;
        }

        Node getHead() {
            return head;
        }

        void replaceToHead(Node elem) {
            assert elem != null;
            assert !isEmpty();
            if (head == elem) {
                return;
            }
            if (tail == elem) {
                tail = tail.prev;
                tail.next = null;
            } else {
                elem.prev.next = elem.next;
                elem.next.prev = elem.prev;
            }
            elem.prev = null;
            elem.next = head;
            head.prev = elem;
            head = elem;
        }

        Node addToHead(K key, V value) {
            Node newNode = new Node(key, value);
            if (isEmpty()) {
                assert head == tail;
                head = tail = newNode;
            } else {
                assert head != null;
                newNode.next = head;
                head.prev = newNode;
                head = newNode;
            }
            return newNode;
        }
    }
}