using System;

namespace QuickSplit.Domain
{
    public class Map
    {
        private int _xCoordenate; 
        private int _yCoordenate; 

        public int XCoordenate
        {
            get => _xCoordenate;
            set
            {
                _xCoordenate = value;
            }
        }

        public int YCoordenate
        {
            get => _yCoordenate;
            set
            {
                _yCoordenate = value;
            }
        }
    }
}




