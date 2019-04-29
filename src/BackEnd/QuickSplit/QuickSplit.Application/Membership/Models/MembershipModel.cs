using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using QuickSplit.Domain;
using QuickSplit.Application.Users.Models;
using QuickSplit.Application.Groups.Models;

namespace QuickSplit.Application.Memberships.Models
{
    public class MembershipModel 
    {
        public MembershipModel()
        {
        }

        public MembershipModel(int userId, int groupId, User user, Group group)
        {
            UserId = userId;
            GroupId = groupId;
            UserModel = new UserModel(user);
            GroupModel = new GroupModel(group);
        }

        public int UserId { get; set; }

        public int GroupId { get; set; }

        public UserModel UserModel;

        public GroupModel GroupModel;
    }
}
