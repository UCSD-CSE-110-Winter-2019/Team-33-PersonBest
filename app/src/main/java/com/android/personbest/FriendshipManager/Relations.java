package com.android.personbest.FriendshipManager;

public class Relations implements FriendshipManager{

    Email2Id email2Id;
    FFireBaseAdapter fireBaseAdapter;

    String from;

    public Relations(String from) {
        this.fireBaseAdapter = new FriendFireBaseAdapter(from);
        this.email2Id = new EmailLookUper();
    }

    public void addFriend(String name,String email) {
        System.err.println(email);
        email2Id.getIdByEmail(email, friendId -> {
            if(friendId != null && !friendId.equals("Unknown")) {
                fireBaseAdapter.addFriendById(name,friendId);
                System.err.println(friendId);
            }
            else{
                System.err.println("NULL USER");
            }
        });
    }

    public void setFriendFireBase(FFireBaseAdapter fFireBaseAdapter){
        this.fireBaseAdapter = fFireBaseAdapter;
    }

    public void setId(String Id){
        this.from = Id;
    }

    /*public List<String> getFriends() {
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(from)
                .collection("friends");

        ArrayList<String> list = new ArrayList<String>();

        ref.addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getLocalizedMessage());
                        return;
                    }

                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                        List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                        documentChanges.forEach(change -> {
                            QueryDocumentSnapshot document = change.getDocument();
                            list.add((String)document.get(from));
                        });
                    }
                });

        return list;
    }

    public CollectionReference getChatHistory(String email1, String email2) {
        String DOCUMENT_KEY;
        if(email1.compareTo(email2) < 0) DOCUMENT_KEY = email1 + " " + email2;
        else DOCUMENT_KEY = email2 + " " + email1;

        return FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGE_KEY);
    }

    private void initMessageUpdateListener() {
        chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING)
                .addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getLocalizedMessage());
                        return;
                    }

                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                        documentChanges.forEach(change -> {
                            QueryDocumentSnapshot document = change.getDocument();
                            sb.append(document.get(FROM_KEY));
                            sb.append(":\n");
                            sb.append(document.get(TEXT_KEY));
                            sb.append("\n");
                            sb.append("---\n");
                        });
                    }
                });
    }

    private void subscribeToNotificationsTopic(String DOCUMENT_KEY) {
        FirebaseMessaging.getInstance().subscribeToTopic(DOCUMENT_KEY)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG, msg);
                        }
                );
    }*/
}
