using System;

namespace QuickSplit.Domain
{
   public class Membership
   {
       public Membership(User user, Group @group)
       {
           User = user;
           userId = user.Id;
           Group = @group;
           groupId = group.Id;
       }

       private int userId;
        private int groupId;
        public virtual User User { get; set; }
        public virtual Group Group { get; set; }

        public int UserId
        {
            get => userId;
            set
            {
                userId = value;
            }
        }

        public int GroupId
        {
            get => groupId;
            set
            {
                groupId = value;
            }
        }

        public Membership()
        {

        }
    }
}


