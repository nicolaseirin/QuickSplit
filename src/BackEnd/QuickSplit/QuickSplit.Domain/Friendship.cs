namespace QuickSplit.Domain
{
    public class Friendship
    {
        private User friend1;
        private User friend2;

        public Friendship()
        {
            
        }
        
        public Friendship(User friend1, User friend2)
        {
            Friend1 = friend1;
            Friend2 = friend2;
        }

        public int Friend1Id { get; private set; }
        public int Friend2Id { get; private set; }

        public User Friend1
        {
            get { return friend1; }
            set
            {
                ValidateNotNull(value);
                friend1 = value;
                Friend1Id = value.Id;
            }
        }

        public User Friend2
        {
            get { return friend2; }
            set
            {
                ValidateNotNull(value);
                friend2 = value;
                Friend2Id = value.Id;
            }
        }

        private void ValidateNotNull(User user)
        {
            if (user == null)
                throw new DomainException("Invalid friendship");
        }
    }
}