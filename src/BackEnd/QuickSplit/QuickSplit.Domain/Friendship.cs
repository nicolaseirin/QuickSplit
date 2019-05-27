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
            Friend1Id = friend1.Id;
            Friend2 = friend2;
            Friend2Id = friend2.Id;
        }

        public int Friend1Id { get; private set; }
        public int Friend2Id { get; private set; }

        public virtual User Friend1 { get; set; }

        public virtual User Friend2 { get; set; }

        private void ValidateNotNull(User user)
        {
            if (user == null)
                throw new DomainException("Invalid friendship");
        }

        protected bool Equals(Friendship other)
        {
            return Equals(friend1, other.friend1) && Equals(friend2, other.friend2);
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (!(obj is Friendship)) return false;
            return Equals((Friendship) obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return ((friend1 != null ? friend1.GetHashCode() : 0) * 397) ^ (friend2 != null ? friend2.GetHashCode() : 0);
            }
        }
    }
}