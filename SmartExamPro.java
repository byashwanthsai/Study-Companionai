/**
 * SmartExam Pro — Java DSA Edition
 *
 * CO1 : Searching & Sorting — Algorithm Analysis, Linear/Binary Search,
 *       Bubble, Insertion, Selection, Merge, Quick Sort
 * CO2 : Lists — List ADT, Singly / Doubly / Circular Linked Lists, Polynomial ADT
 * CO3 : Stacks & Queues — Stack ADT, Infix→Postfix, Balancing Symbols,
 *       Queue ADT, Circular Queue, Deque, LinkedList-backed Stack & Queue
 * CO4 : Hashing & Trees — Binary Min-Heap, Hash Function,
 *       Separate Chaining, Open Addressing, Rehashing
 *
 * Running Time Reference:
 *   Linear Search O(n) | Binary Search O(log n) | Bubble/Insertion/Selection O(n²)
 *   Merge Sort O(n log n) | Quick Sort O(n log n) avg | Heap insert/delete O(log n)
 */
import java.util.*;

// ─────────────────────────────────────────────────────────────────
// CO1 — MODEL  |  QuizQuestion is the core data unit for search/sort
// ─────────────────────────────────────────────────────────────────
class QuizQuestion {
    int id, difficulty, answerIndex;
    String topic, questionText;
    String[] options; // 4 options: A B C D

    QuizQuestion(int id, String topic, String q, String[] opts, int ans, int diff) {
        this.id = id; this.topic = topic; this.questionText = q;
        this.options = opts; this.answerIndex = ans; this.difficulty = diff;
    }

    @Override
    public String toString() { return "[Q" + id + " | " + topic + "] " + questionText; }
}

// ─────────────────────────────────────────────────────────────────
// CO1 — SEARCHING
// ─────────────────────────────────────────────────────────────────
class Search {

    // CO1: Linear Search — O(n), no pre-condition needed
    static List<QuizQuestion> linear(List<QuizQuestion> bank, String keyword) {
        List<QuizQuestion> result = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (QuizQuestion q : bank)
            if (q.topic.toLowerCase().contains(kw)) result.add(q);
        return result;
    }

    // CO1: Binary Search — O(log n), list must be sorted by id
    //      Recurrence: T(n) = T(n/2) + O(1)
    static QuizQuestion binary(List<QuizQuestion> sorted, int targetId) {
        int lo = 0, hi = sorted.size() - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2; // avoids overflow
            int midId = sorted.get(mid).id;
            if      (midId == targetId) return sorted.get(mid);
            else if (midId  < targetId) lo = mid + 1;
            else                        hi = mid - 1;
        }
        return null;
    }
}

// ─────────────────────────────────────────────────────────────────
// CO1 — SORTING  (all five algorithms)
// ─────────────────────────────────────────────────────────────────
class Sort {

    // CO1: Bubble Sort — O(n²), stable; swap flag gives O(n) best case
    static void bubble(List<QuizQuestion> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).difficulty > list.get(j + 1).difficulty) {
                    Collections.swap(list, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // CO1: early termination optimisation
        }
    }

    // CO1: Insertion Sort — O(n²) worst, O(n) best on nearly-sorted input
    static void insertion(List<QuizQuestion> list) {
        for (int i = 1; i < list.size(); i++) {
            QuizQuestion key = list.get(i);
            int j = i - 1;
            while (j >= 0 && list.get(j).id > key.id) {
                list.set(j + 1, list.get(j)); j--;
            }
            list.set(j + 1, key);
        }
    }

    // CO1: Selection Sort — O(n²) always, finds minimum each pass
    static void selection(List<QuizQuestion> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < list.size(); j++)
                if (list.get(j).id < list.get(minIdx).id) minIdx = j;
            Collections.swap(list, i, minIdx);
        }
    }

    // CO1: Merge Sort — O(n log n) always, T(n)=2T(n/2)+O(n) [Master Theorem]
    static List<QuizQuestion> merge(List<QuizQuestion> list) {
        if (list.size() <= 1) return list;
        int mid = list.size() / 2;
        List<QuizQuestion> left  = merge(new ArrayList<>(list.subList(0, mid)));
        List<QuizQuestion> right = merge(new ArrayList<>(list.subList(mid, list.size())));
        List<QuizQuestion> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) // CO1: merge step O(n)
            result.add(left.get(i).difficulty <= right.get(j).difficulty ? left.get(i++) : right.get(j++));
        while (i < left.size())  result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    // CO1: Quick Sort — O(n log n) avg, O(n²) worst (bad pivot)
    static void quick(List<QuizQuestion> list, int lo, int hi) {
        if (lo < hi) { int p = partition(list, lo, hi); quick(list, lo, p-1); quick(list, p+1, hi); }
    }

    // CO1: Partition — pivot = last element, places it at correct index
    private static int partition(List<QuizQuestion> list, int lo, int hi) {
        int pivot = list.get(hi).difficulty, i = lo - 1;
        for (int j = lo; j < hi; j++)
            if (list.get(j).difficulty <= pivot) { i++; Collections.swap(list, i, j); }
        Collections.swap(list, i + 1, hi);
        return i + 1;
    }
}

// ─────────────────────────────────────────────────────────────────
// CO2 — NODE  (shared by Singly, Doubly, Circular lists & Deque)
// ─────────────────────────────────────────────────────────────────
class Node<T> {
    T data; Node<T> next, prev;
    Node(T data) { this.data = data; }
}

// ─────────────────────────────────────────────────────────────────
// CO2 — SINGLY LINKED LIST  |  HEAD → N1 → N2 → null
// addFirst O(1)  |  addLast O(n)  |  removeFirst O(1)
// ─────────────────────────────────────────────────────────────────
class SinglyLL<T> {
    Node<T> head; int size;

    // CO2: addFirst — O(1), new node points to current head
    void addFirst(T data) { Node<T> n = new Node<>(data); n.next = head; head = n; size++; }

    // CO2: addLast — O(n), traverse to tail then insert
    void addLast(T data) {
        Node<T> n = new Node<>(data);
        if (head == null) { head = n; size++; return; }
        Node<T> cur = head; while (cur.next != null) cur = cur.next;
        cur.next = n; size++;
    }

    // CO2: removeFirst — O(1)
    T removeFirst() {
        if (head == null) throw new NoSuchElementException("List empty");
        T data = head.data; head = head.next; size--; return data;
    }

    void display() {
        System.out.print("SinglyLL  : HEAD");
        for (Node<T> c = head; c != null; c = c.next) System.out.print(" → " + c.data);
        System.out.println(" → null");
    }
}

// ─────────────────────────────────────────────────────────────────
// CO2 — DOUBLY LINKED LIST  |  null ← N1 ⇄ N2 ⇄ Nn → null
// addFirst O(1)  |  addLast O(1) via tail pointer  |  removeLast O(1)
// ─────────────────────────────────────────────────────────────────
class DoublyLL<T> {
    Node<T> head, tail; int size;

    // CO2: addFirst — O(1), maintain both head and prev links
    void addFirst(T data) {
        Node<T> n = new Node<>(data);
        if (head == null) { head = tail = n; }
        else { n.next = head; head.prev = n; head = n; }
        size++;
    }

    // CO2: addLast — O(1) thanks to tail pointer
    void addLast(T data) {
        Node<T> n = new Node<>(data);
        if (tail == null) { head = tail = n; }
        else { n.prev = tail; tail.next = n; tail = n; }
        size++;
    }

    // CO2: removeFirst — O(1), update head and its prev pointer
    T removeFirst() {
        if (head == null) throw new NoSuchElementException("List empty");
        T data = head.data; head = head.next;
        if (head != null) head.prev = null; else tail = null;
        size--; return data;
    }

    void display() {
        System.out.print("DoublyLL  : null");
        for (Node<T> c = head; c != null; c = c.next) System.out.print(" ⇄ " + c.data);
        System.out.println(" ⇄ null");
    }
}

// ─────────────────────────────────────────────────────────────────
// CO2 — CIRCULARLY LINKED LIST  |  last.next == head  (no null)
// Used for round-robin topic scheduling in quiz module
// ─────────────────────────────────────────────────────────────────
class CircularLL<T> {
    Node<T> tail; int size;

    // CO2: addLast — O(1), tail.next always points back to head
    void addLast(T data) {
        Node<T> n = new Node<>(data);
        if (tail == null) { n.next = n; tail = n; }
        else { n.next = tail.next; tail.next = n; tail = n; }
        size++;
    }

    // CO2: removeFirst — O(1), relink tail to new head
    T removeFirst() {
        if (tail == null) throw new NoSuchElementException("List empty");
        Node<T> head = tail.next; T data = head.data;
        if (tail == head) tail = null; else tail.next = head.next;
        size--; return data;
    }

    void display() {
        if (tail == null) { System.out.println("CircularLL : (empty)"); return; }
        System.out.print("CircularLL : ");
        Node<T> c = tail.next;
        do { System.out.print(c.data + " → "); c = c.next; } while (c != tail.next);
        System.out.println("(back to head)");
    }
}

// ─────────────────────────────────────────────────────────────────
// CO2 — POLYNOMIAL ADT  |  P(x) = Σ coeff * x^exp
// Stored as sorted linked list of terms (descending exponent)
// Application: Score improvement projection curve in dashboard
// ─────────────────────────────────────────────────────────────────
class Poly {
    private static class Term { double coeff; int exp; Term next; Term(double c, int e){coeff=c;exp=e;} }
    private Term head;

    // CO2: addTerm — insert in descending order, merge like-degree terms
    void addTerm(double coeff, int exp) {
        Term t = new Term(coeff, exp);
        if (head == null || exp > head.exp) { t.next = head; head = t; return; }
        Term cur = head;
        while (cur.next != null && cur.next.exp > exp) cur = cur.next;
        if (cur.next != null && cur.next.exp == exp) cur.next.coeff += coeff;
        else { t.next = cur.next; cur.next = t; }
    }

    // CO2: evaluate P(x) at given x using direct summation, O(n)
    double eval(double x) {
        double r = 0;
        for (Term c = head; c != null; c = c.next) r += c.coeff * Math.pow(x, c.exp);
        return r;
    }

    void display() {
        System.out.print("Polynomial : ");
        for (Term c = head; c != null; c = c.next) System.out.printf("%.1fx^%d%s", c.coeff, c.exp, c.next!=null?" + ":"");
        System.out.println();
    }
}

// ─────────────────────────────────────────────────────────────────
// CO3 — STACK (Linked List)  |  LIFO  |  push/pop/peek O(1)
// ─────────────────────────────────────────────────────────────────
class LinkedStack<T> {
    private Node<T> top; private int size;

    // CO3: push — insert new node at top, O(1)
    void push(T data) { Node<T> n = new Node<>(data); n.next = top; top = n; size++; }

    // CO3: pop — remove top node, O(1)
    T pop() {
        if (top == null) throw new EmptyStackException();
        T data = top.data; top = top.next; size--; return data;
    }

    // CO3: peek — view top without removing, O(1)
    T peek() { if (top == null) throw new EmptyStackException(); return top.data; }

    boolean isEmpty() { return top == null; }
}

// ─────────────────────────────────────────────────────────────────
// CO3 — STACK APPLICATION 1: Balancing Symbols  O(n)
// Push opening brackets; pop and match on every closing bracket.
// ─────────────────────────────────────────────────────────────────
class BalSymbols {
    static boolean check(String expr) {
        LinkedStack<Character> stack = new LinkedStack<>();
        for (char c : expr.toCharArray()) {
            if ("({[".indexOf(c) >= 0) { stack.push(c); } // CO3: push opener
            else if (")}]".indexOf(c) >= 0) {
                if (stack.isEmpty()) return false;
                char top = stack.pop(); // CO3: pop and verify match
                if ((c==')' && top!='(') || (c=='}' && top!='{') || (c==']' && top!='[')) return false;
            }
        }
        return stack.isEmpty(); // CO3: balanced only if stack is empty
    }
}

// ─────────────────────────────────────────────────────────────────
// CO3 — STACK APPLICATION 2: Infix → Postfix (Shunting-Yard) O(n)
//       + Postfix Evaluation using operand stack
// ─────────────────────────────────────────────────────────────────
class InfixPostfix {
    private static int prec(char c) { return (c=='+'||c=='-')?1:(c=='*'||c=='/')?2:(c=='^')?3:0; }

    // CO3: Infix to Postfix conversion using operator stack
    static String convert(String infix) {
        LinkedStack<Character> stack = new LinkedStack<>();
        StringBuilder output = new StringBuilder();
        for (char c : infix.toCharArray()) {
            if (Character.isLetterOrDigit(c)) { output.append(c).append(' '); }
            else if (c == '(') { stack.push(c); }
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') output.append(stack.pop()).append(' ');
                if (!stack.isEmpty()) stack.pop(); // discard '('
            } else {
                while (!stack.isEmpty() && prec(stack.peek()) >= prec(c)) output.append(stack.pop()).append(' ');
                stack.push(c);
            }
        }
        while (!stack.isEmpty()) output.append(stack.pop()).append(' ');
        return output.toString().trim();
    }

    // CO3: Postfix Evaluation — push operands, pop two on each operator
    static double evalPostfix(String postfix) {
        LinkedStack<Double> stack = new LinkedStack<>();
        for (String token : postfix.split(" ")) {
            if (token.matches("-?\\d+(\\.\\d+)?")) { stack.push(Double.parseDouble(token)); }
            else { double b = stack.pop(), a = stack.pop();
                switch (token.charAt(0)) {
                    case '+': stack.push(a+b); break; case '-': stack.push(a-b); break;
                    case '*': stack.push(a*b); break; case '/': stack.push(a/b); break;
                }
            }
        }
        return stack.pop();
    }
}

// ─────────────────────────────────────────────────────────────────
// CO3 — QUEUE (Linked List)  |  FIFO  |  enqueue/dequeue O(1)
// ─────────────────────────────────────────────────────────────────
class LinkedQueue<T> {
    private Node<T> front, rear; private int size;

    // CO3: enqueue — add to rear, O(1)
    void enqueue(T data) {
        Node<T> n = new Node<>(data);
        if (rear != null) rear.next = n; rear = n;
        if (front == null) front = n; size++;
    }

    // CO3: dequeue — remove from front, O(1)
    T dequeue() {
        if (front == null) throw new NoSuchElementException("Queue empty");
        T data = front.data; front = front.next;
        if (front == null) rear = null; size--; return data;
    }

    boolean isEmpty() { return front == null; }
    int     size()    { return size; }
}

// ─────────────────────────────────────────────────────────────────
// CO3 — CIRCULAR QUEUE (Array)
// front/rear wrap via modulo: rear=(rear+1)%cap, front=(front+1)%cap
// Avoids O(n) shifting of a plain array queue
// ─────────────────────────────────────────────────────────────────
class CircularQueue<T> {
    private final Object[] arr; private int front, rear, size, cap;

    CircularQueue(int capacity) { cap = capacity; arr = new Object[cap]; rear = -1; }

    // CO3: enqueue — O(1), circular increment via modulo
    void enqueue(T data) {
        if (size == cap) throw new RuntimeException("Queue Overflow");
        rear = (rear + 1) % cap; arr[rear] = data; size++;
    }

    // CO3: dequeue — O(1), circular increment via modulo
    @SuppressWarnings("unchecked")
    T dequeue() {
        if (size == 0) throw new NoSuchElementException("Queue Underflow");
        T data = (T) arr[front]; front = (front + 1) % cap; size--; return data;
    }

    boolean isEmpty() { return size == 0; }
}

// ─────────────────────────────────────────────────────────────────
// CO3 — DEQUE (Double-Ended Queue via Doubly Linked List)
// Insert/remove from both ends in O(1)
// ─────────────────────────────────────────────────────────────────
class Deque<T> {
    private Node<T> head, tail;

    // CO3: addFirst and addLast — O(1)
    void addFirst(T d) { Node<T> n=new Node<>(d); if(head==null){head=tail=n;} else{n.next=head;head.prev=n;head=n;} }
    void addLast(T d)  { Node<T> n=new Node<>(d); if(tail==null){head=tail=n;} else{n.prev=tail;tail.next=n;tail=n;} }

    // CO3: removeFirst and removeLast — O(1)
    T removeFirst() {
        if (head == null) throw new NoSuchElementException();
        T d = head.data; head = head.next;
        if (head != null) head.prev = null; else tail = null; return d;
    }
    T removeLast() {
        if (tail == null) throw new NoSuchElementException();
        T d = tail.data; tail = tail.prev;
        if (tail != null) tail.next = null; else head = null; return d;
    }
}

// ─────────────────────────────────────────────────────────────────
// CO4 — BINARY MIN-HEAP  (Priority Queue)
// Complete binary tree stored as array.
// Parent=(i-1)/2  |  Left child=2i+1  |  Right child=2i+2
// insert O(log n) siftUp  |  deleteMin O(log n) siftDown
// Application: Schedule students with most wrong answers first
// ─────────────────────────────────────────────────────────────────
class MinHeap {
    private final List<int[]>  h     = new ArrayList<>();
    private final List<String> names = new ArrayList<>();

    // CO4: insert — append at end, siftUp restores heap property
    void insert(String name, int priority) { h.add(new int[]{priority}); names.add(name); siftUp(h.size()-1); }

    // CO4: deleteMin — swap root with last, remove last, siftDown
    String deleteMin() {
        if (h.isEmpty()) throw new NoSuchElementException("Heap empty");
        String min = names.get(0); int last = h.size()-1;
        swap(0, last); h.remove(last); names.remove(last);
        if (!h.isEmpty()) siftDown(0); return min;
    }

    String peekMin() { return names.get(0) + " (priority=" + h.get(0)[0] + ")"; }
    boolean isEmpty() { return h.isEmpty(); }

    // CO4: siftUp — move node up while smaller than parent, O(log n)
    private void siftUp(int i) {
        while (i > 0) { int p = (i-1)/2; // CO4: parent formula
            if (h.get(i)[0] < h.get(p)[0]) { swap(i, p); i = p; } else break;
        }
    }

    // CO4: siftDown — move node down to correct position, O(log n)
    private void siftDown(int i) {
        int n = h.size();
        while (true) {
            int l = 2*i+1, r = 2*i+2, s = i; // CO4: child index formulas
            if (l < n && h.get(l)[0] < h.get(s)[0]) s = l;
            if (r < n && h.get(r)[0] < h.get(s)[0]) s = r;
            if (s == i) break; swap(i, s); i = s;
        }
    }

    private void swap(int a, int b) { Collections.swap(h, a, b); Collections.swap(names, a, b); }
}

// ─────────────────────────────────────────────────────────────────
// CO4 — HASH TABLE: Separate Chaining
// Hash function: h(k) = Σ(31^i * c_i) mod M  (polynomial rolling)
// Each slot holds a linked chain of entries.
// Average O(1) search when load factor λ = n/M is small.
// Rehashing triggered at λ ≥ 0.7 — capacity doubles to next prime.
// ─────────────────────────────────────────────────────────────────
class HashTable {
    private static class Entry { String key; Object val; Entry next; Entry(String k,Object v){key=k;val=v;} }
    private Entry[] table; private int size, cap;

    HashTable(int capacity) { cap = capacity; table = new Entry[cap]; }

    // CO4: hash function — polynomial rolling hash
    private int hash(String key) { int h=0; for(char c:key.toCharArray()) h=(h*31+c)%cap; return Math.abs(h); }

    // CO4: put — O(1) amortised; triggers rehash when λ ≥ 0.7
    void put(String key, Object value) {
        if ((double) size / cap >= 0.7) rehash(); // CO4: check load factor
        int idx = hash(key); Entry cur = table[idx];
        while (cur != null) { if (cur.key.equals(key)){cur.val=value;return;} cur=cur.next; }
        Entry e = new Entry(key, value); e.next = table[idx]; table[idx] = e; size++;
    }

    // CO4: get — O(1) average, traverse chain at hashed index
    Object get(String key) {
        Entry cur = table[hash(key)];
        while (cur != null) { if (cur.key.equals(key)) return cur.val; cur=cur.next; }
        return null;
    }

    // CO4: Rehashing — rebuild at doubled prime capacity, amortised O(1)
    private void rehash() {
        int oldCap = cap; Entry[] old = table;
        cap = nextPrime(cap * 2); table = new Entry[cap]; size = 0;
        for (int i = 0; i < oldCap; i++)
            for (Entry c = old[i]; c != null; c = c.next) put(c.key, c.val); // CO4: re-insert
        System.out.println("[CO4-Rehash] New table capacity: " + cap);
    }

    private int nextPrime(int n) { while (!isPrime(n)) n++; return n; }
    private boolean isPrime(int n) { if(n<2)return false; for(int i=2;i*i<=n;i++) if(n%i==0)return false; return true; }
    int size() { return size; }
}

// ─────────────────────────────────────────────────────────────────
// CO4 — OPEN ADDRESSING: Linear Probing
// Probe sequence: h(k, i) = (h(k) + i) mod M
// DELETED tombstone preserves probe chains after removals.
// ─────────────────────────────────────────────────────────────────
class OpenHashTable {
    private String[] keys; private Object[] vals; private int size, cap;
    private static final String DELETED = "__DEL__";

    OpenHashTable(int capacity) { cap=capacity; keys=new String[cap]; vals=new Object[cap]; }

    private int hash(String key) { int h=0; for(char c:key.toCharArray()) h=(h*31+c)%cap; return Math.abs(h); }

    // CO4: put — linear probing on collision, O(1) average
    void put(String key, Object value) {
        int idx = hash(key);
        for (int i = 0; i < cap; i++) {
            int p = (idx + i) % cap; // CO4: linear probe sequence
            if (keys[p] == null || keys[p].equals(DELETED)) { keys[p]=key; vals[p]=value; size++; return; }
            if (keys[p].equals(key)) { vals[p]=value; return; }
        }
    }

    // CO4: get — probe until key found or empty slot, O(1) average
    Object get(String key) {
        int idx = hash(key);
        for (int i = 0; i < cap; i++) {
            int p = (idx + i) % cap;
            if (keys[p] == null) return null;
            if (keys[p].equals(key)) return vals[p];
        }
        return null;
    }
}

// ─────────────────────────────────────────────────────────────────
// SMARTEXAM APP — Application layer
// Mirrors all HTML/JS SmartExam Pro features using Java DSA
// ─────────────────────────────────────────────────────────────────
class SmartExamApp {

    // CO4: Hash tables as primary data store
    private final HashTable     userStore    = new HashTable(11);    // user accounts
    private final OpenHashTable sessionStore = new OpenHashTable(7); // session tokens

    // CO2: Doubly Linked List for note storage (addFirst O(1))
    final DoublyLL<String> notes = new DoublyLL<>();

    // CO4: Binary Min-Heap schedules students who need most practice
    private final MinHeap heap = new MinHeap();

    // CO3: LinkedQueue delivers quiz questions in FIFO order
    private final LinkedQueue<QuizQuestion> quizQueue = new LinkedQueue<>();

    List<QuizQuestion> bank;

    private String  loggedInUser = null;
    private boolean premium      = false;
    private int     noteCount = 0, uploads = 0, quizzesDone = 0, totalScore = 0;
    static final int FREE_NOTES = 5, FREE_UPLOADS = 3;

    SmartExamApp() {
        initBank();
        initScoreModel();
    }

    // CO1: Build bank then sort by difficulty using Merge Sort — O(n log n)
    private void initBank() {
        bank = new ArrayList<>(Arrays.asList(
            new QuizQuestion(1,  "Biology",    "What is photosynthesis?",    new String[]{"Making food from sunlight","Breaking down food","Cell division","Respiration"}, 0, 1),
            new QuizQuestion(2,  "Astronomy",  "Planet closest to the Sun?", new String[]{"Venus","Earth","Mercury","Mars"}, 2, 1),
            new QuizQuestion(3,  "Chemistry",  "What is H₂O?",               new String[]{"Hydrogen Peroxide","Water","Salt","Carbon Dioxide"}, 1, 1),
            new QuizQuestion(4,  "Literature", "Who wrote Hamlet?",          new String[]{"Charles Dickens","Jane Austen","William Shakespeare","Mark Twain"}, 2, 2),
            new QuizQuestion(5,  "Physics",    "Speed of light?",            new String[]{"3×10⁸ m/s","3×10⁶ m/s","3×10⁵ m/s","3×10¹⁰ m/s"}, 0, 3),
            new QuizQuestion(6,  "Biology",    "Powerhouse of the cell?",    new String[]{"Nucleus","Ribosome","Mitochondria","Golgi body"}, 2, 1),
            new QuizQuestion(7,  "Physics",    "Newton's 1st Law?",          new String[]{"F=ma","Inertia","Action-reaction","Gravity"}, 1, 2),
            new QuizQuestion(8,  "Math",       "2 + 2 × 2 = ?",             new String[]{"8","6","4","2"}, 1, 2),
            new QuizQuestion(9,  "Biology",    "Gas plants absorb?",         new String[]{"Oxygen","Nitrogen","CO₂","Hydrogen"}, 2, 1),
            new QuizQuestion(10, "Geography",  "Capital of France?",         new String[]{"Rome","Berlin","Madrid","Paris"}, 3, 1)
        ));
        bank = Sort.merge(bank); // CO1: Merge Sort
        System.out.println("[CO1-MergeSort] Question bank sorted by difficulty.\n");
    }

    // CO2: Score projection polynomial P(x) = 0.5x² + 2x + 10
    private void initScoreModel() {
        Poly scoreModel = new Poly();
        scoreModel.addTerm(0.5, 2); scoreModel.addTerm(2.0, 1); scoreModel.addTerm(10.0, 0);
        scoreModel.display();
        System.out.printf("[CO2-Polynomial] Projected score after 5 sessions: %.1f%%\n\n", scoreModel.eval(5));
    }

    // CO4: Signup — insert user into Separate Chaining hash table
    void signup(String email, String name) {
        userStore.put(email, name);
        System.out.println("[CO4-SepChaining] Registered: " + name);
    }

    // CO4: Login — O(1) hash lookup; session stored via Open Addressing
    boolean login(String email) {
        Object u = userStore.get(email); // CO4: O(1) amortised
        if (u == null) { System.out.println("[Auth] Not found."); return false; }
        loggedInUser = (String) u;
        sessionStore.put("tok_" + email.hashCode(), loggedInUser); // CO4: open addressing
        System.out.println("[CO4-OpenAddr] Session created for: " + loggedInUser);
        return true;
    }

    void logout() { loggedInUser = null; premium = false; System.out.println("[Auth] Logged out."); }

    // CO2: Add note — prepend to Doubly Linked List, O(1)
    void addNote(String note) {
        if (!premium && noteCount >= FREE_NOTES) { System.out.println("[Notes] Free limit reached!"); return; }
        notes.addFirst(note); noteCount++; // CO2: DoublyLL O(1)
        System.out.println("[CO2-DoublyLL] Note saved: " + note);
    }

    // CO4: Upload — document indexed in hash table as key→content
    void upload(String filename, String content) {
        if (!premium && uploads >= FREE_UPLOADS) { System.out.println("[Upload] Limit reached!"); return; }
        userStore.put(filename, content); uploads++; // CO4: hash table
        System.out.println("[CO4-HashTable] Document indexed: " + filename);
    }

    // CO1 + CO3 + CO4: Run Quiz
    void runQuiz(String topic, int numQ) {
        System.out.println("\n[Quiz] Topic: " + topic + " | Questions: " + numQ);

        // CO1: Linear Search O(n) — find matching questions by topic
        List<QuizQuestion> matched = Search.linear(bank, topic);
        if (matched.isEmpty()) { System.out.println("[CO1-LinearSearch] No match. Using full bank."); matched = new ArrayList<>(bank); }
        else System.out.println("[CO1-LinearSearch] Found: " + matched.size() + " matching questions.");

        // CO1: Insertion Sort O(n²) — ideal for small matched sets
        Sort.insertion(matched);
        System.out.println("[CO1-InsertionSort] Sorted matched questions by id.");

        List<QuizQuestion> selected = matched.subList(0, Math.min(numQ, matched.size()));

        // CO3: Enqueue selected questions into LinkedQueue (FIFO)
        for (QuizQuestion q : selected) quizQueue.enqueue(q); // CO3: O(1)

        int score = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n╔═══════════════════════════╗");
        System.out.println("║        QUIZ TIME! ⚡       ║");
        System.out.println("╚═══════════════════════════╝");

        // CO3: Dequeue one question at a time — FIFO delivery
        while (!quizQueue.isEmpty()) {
            QuizQuestion q = quizQueue.dequeue(); // CO3: O(1)
            System.out.println("\nQ: " + q.questionText + "  [Difficulty: " + q.difficulty + "]");
            for (int i = 0; i < q.options.length; i++) System.out.printf("  %c) %s\n", 'A'+i, q.options[i]);
            System.out.print("Your Answer (0-3): "); int ans = -1;
            try { ans = sc.nextInt(); } catch (Exception e) { sc.nextLine(); }
            if (ans == q.answerIndex) { System.out.println("  ✅ Correct!"); score++; }
            else System.out.println("  ❌ Wrong! Ans: " + (char)('A'+q.answerIndex) + ") " + q.options[q.answerIndex]);
        }

        int pct = (int)((double) score / selected.size() * 100);
        heap.insert(loggedInUser != null ? loggedInUser : "Guest", selected.size() - score); // CO4: heap insert O(log n)
        quizzesDone++; totalScore += pct;
        System.out.printf("\n[Result] %d/%d (%d%%) — %s\n", score, selected.size(), pct,
            pct>=80 ? "🏆 Excellent!" : pct>=60 ? "👍 Good job!" : "📚 Keep studying!");
        System.out.println("[CO4-MinHeap] Most urgent student: " + heap.peekMin());
    }

    void upgradePremium() { premium = true; System.out.println("\n[Premium] 🎉 Premium Activated! Unlimited access."); }

    void dashboard() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║      SmartExam Pro — Dashboard    ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.printf( "║  User      : %-20s║\n", loggedInUser != null ? loggedInUser : "Guest");
        System.out.printf( "║  Plan      : %-20s║\n", premium ? "PREMIUM ⭐" : "FREE");
        System.out.printf( "║  Notes     : %-20d║\n", noteCount);
        System.out.printf( "║  Uploads   : %-20d║\n", uploads);
        System.out.printf( "║  Quizzes   : %-20d║\n", quizzesDone);
        System.out.printf( "║  Avg Score : %-20s║\n", quizzesDone > 0 ? (totalScore/quizzesDone) + "%" : "—");
        System.out.println("╚══════════════════════════════════╝");
    }
}

// ─────────────────────────────────────────────────────────────────
// MAIN — Entry point; demonstrates all CO1–CO4 concepts
// ─────────────────────────────────────────────────────────────────
public class SmartExamPro {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║    SmartExam Pro — Java DSA Edition       ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║  CO1: Searching & Sorting                 ║");
        System.out.println("║  CO2: Lists & Polynomial ADT              ║");
        System.out.println("║  CO3: Stacks & Queues                     ║");
        System.out.println("║  CO4: Hashing & Priority Queue (Heap)     ║");
        System.out.println("╚═══════════════════════════════════════════╝\n");

        SmartExamApp app = new SmartExamApp();

        // ── CO4: Auth via Hash Tables ────────────────────────────────────
        System.out.println("─── CO4: Auth (Separate Chaining + Open Addressing) ───");
        app.signup("alice@exam.com", "Alice");
        app.login("alice@exam.com");
        app.dashboard();

        // ── CO1: Sorting Demos ───────────────────────────────────────────
        System.out.println("\n─── CO1: Sorting Algorithms ────────────────────────────");
        List<QuizQuestion> demo = new ArrayList<>(Arrays.asList(
            new QuizQuestion(3, "T", "Q3", new String[]{"a","b","c","d"}, 0, 3),
            new QuizQuestion(1, "T", "Q1", new String[]{"a","b","c","d"}, 0, 1),
            new QuizQuestion(2, "T", "Q2", new String[]{"a","b","c","d"}, 0, 2)
        ));

        Sort.bubble(demo);
        System.out.print("[CO1-BubbleSort     O(n²)]   difficulty: ");
        demo.forEach(q -> System.out.print(q.difficulty + " ")); System.out.println();

        Collections.shuffle(demo); Sort.selection(demo);
        System.out.print("[CO1-SelectionSort  O(n²)]   id:         ");
        demo.forEach(q -> System.out.print(q.id + " ")); System.out.println();

        Collections.shuffle(demo); Sort.insertion(demo);
        System.out.print("[CO1-InsertionSort  O(n²)]   id:         ");
        demo.forEach(q -> System.out.print(q.id + " ")); System.out.println();

        Collections.shuffle(demo); demo = Sort.merge(demo);
        System.out.print("[CO1-MergeSort   O(n log n)] difficulty: ");
        demo.forEach(q -> System.out.print(q.difficulty + " ")); System.out.println();

        Collections.shuffle(demo); Sort.quick(demo, 0, demo.size() - 1);
        System.out.print("[CO1-QuickSort   O(n log n)] difficulty: ");
        demo.forEach(q -> System.out.print(q.difficulty + " ")); System.out.println();

        // ── CO1: Searching Demos ─────────────────────────────────────────
        System.out.println("\n─── CO1: Searching Algorithms ──────────────────────────");

        List<QuizQuestion> biologyQs = Search.linear(app.bank, "Biology"); // CO1: O(n)
        System.out.println("[CO1-LinearSearch   O(n)]     Biology questions found: " + biologyQs.size());
        biologyQs.forEach(q -> System.out.println("  " + q));

        List<QuizQuestion> sortedById = new ArrayList<>(app.bank);
        Sort.insertion(sortedById); // pre-condition for binary search
        QuizQuestion q5 = Search.binary(sortedById, 5);                    // CO1: O(log n)
        System.out.println("[CO1-BinarySearch   O(log n)] id=5: " + (q5 != null ? q5 : "Not found"));

        // ── CO2: Linked Lists & Polynomial ───────────────────────────────
        System.out.println("\n─── CO2: Linked Lists & Polynomial ADT ─────────────────");

        SinglyLL<String> sll = new SinglyLL<>();
        sll.addLast("Note A"); sll.addLast("Note B"); sll.addFirst("Note Z");
        sll.display(); // CO2: Singly Linked List

        DoublyLL<Integer> dll = new DoublyLL<>();
        dll.addLast(10); dll.addLast(20); dll.addLast(30);
        dll.display(); // CO2: Doubly Linked List

        CircularLL<String> cll = new CircularLL<>();
        cll.addLast("Math"); cll.addLast("Science"); cll.addLast("English");
        cll.display(); // CO2: Circularly Linked List

        Poly poly = new Poly(); // CO2: Polynomial ADT — 3x³ + 5x² + 2
        poly.addTerm(3, 3); poly.addTerm(5, 2); poly.addTerm(2, 0);
        poly.display();
        System.out.printf("[CO2-Polynomial]  P(2) = %.1f\n", poly.eval(2));

        // ── CO3: Stack Applications ──────────────────────────────────────
        System.out.println("\n─── CO3: Stack Applications ─────────────────────────────");

        for (String e : new String[]{"(3 + [4 * {2}])", "(2 + 3]", "((a + b)"})
            System.out.println("[CO3-BalancingSymbols]  '" + e + "'  →  "
                + (BalSymbols.check(e) ? "BALANCED ✅" : "UNBALANCED ❌"));

        System.out.println();
        for (String inf : new String[]{"A+B*C", "(A+B)*C"}) {
            String pf = InfixPostfix.convert(inf); // CO3: Infix → Postfix
            System.out.println("[CO3-InfixToPostfix]    '" + inf + "'  →  '" + pf + "'");
        }
        System.out.println("[CO3-PostfixEval]       '3 4 2 * +'  =  " + InfixPostfix.evalPostfix("3 4 2 * +"));

        // ── CO3: Queue Applications ──────────────────────────────────────
        System.out.println("\n─── CO3: Queue Applications ─────────────────────────────");

        CircularQueue<String> cq = new CircularQueue<>(4); // CO3: Circular Queue
        for (String s : new String[]{"Biology", "Physics", "Math", "History"}) cq.enqueue(s);
        System.out.print("[CO3-CircularQueue]  Round-robin: ");
        while (!cq.isEmpty()) System.out.print(cq.dequeue() + "  "); System.out.println();

        Deque<String> dq = new Deque<>();  // CO3: Deque
        dq.addFirst("Note1"); dq.addLast("Note2"); dq.addFirst("Note0");
        System.out.println("[CO3-Deque]  removeFirst: " + dq.removeFirst()
            + "  |  removeLast: " + dq.removeLast());

        // ── CO4: Hashing & Priority Queue ────────────────────────────────
        System.out.println("\n─── CO4: Hashing & Priority Queue (Heap) ───────────────");

        HashTable ht = new HashTable(5); // CO4: Separate Chaining; triggers rehash
        ht.put("Alice","95"); ht.put("Bob","88"); ht.put("Charlie","72");
        ht.put("Dave","81"); ht.put("Eve","90");
        System.out.println("[CO4-SepChaining]  Alice = " + ht.get("Alice") + "  |  size: " + ht.size());

        OpenHashTable oht = new OpenHashTable(7); // CO4: Open Addressing (Linear Probing)
        oht.put("q1","easy"); oht.put("q2","medium"); oht.put("q3","hard");
        System.out.println("[CO4-OpenAddr]     q2 difficulty = " + oht.get("q2"));

        MinHeap mh = new MinHeap(); // CO4: Binary Min-Heap
        mh.insert("Alice", 3); mh.insert("Bob", 1); mh.insert("Charlie", 2);
        System.out.println("[CO4-MinHeap]      deleteMin → " + mh.deleteMin()
            + "  |  next → " + mh.deleteMin());

        // ── App: Notes & Uploads ─────────────────────────────────────────
        System.out.println("\n─── App: Notes & Uploads ────────────────────────────────");
        app.addNote("Photosynthesis: plants produce food using sunlight"); // CO2: DoublyLL
        app.addNote("Newton's 3 Laws of Motion");
        app.upload("bio_notes.pdf",  "Photosynthesis, Mitosis, DNA");      // CO4: HashTable
        app.upload("physics.pdf",    "Newton, Waves, Optics");
        app.notes.display();

        // ── CO1 + CO3 + CO4: Interactive Quiz ────────────────────────────
        System.out.println("\n─── Interactive Quiz (CO1 + CO3 + CO4) ─────────────────");
        app.runQuiz("Biology", 3);

        // ── Premium Upgrade & Final Dashboard ────────────────────────────
        app.upgradePremium();
        app.addNote("Premium note — unlimited!");
        app.dashboard();

        System.out.println("\n✅ All CO1–CO4 DSA concepts implemented and demonstrated!");
    }
}