
(*
This program prints the prime factorization of a given number.
This program has almost all the rules.
*)

class Main inherits IO {
    main(): Object {
        let n:Int,
        	i : Int  <- 2,
        	c:Int <- 0
        in
        {
            out_string("Please enter the number\n");
            n <- in_int();
            out_string("adas\n");
            while(i < n+1) loop
            {
                if(prime(i)) then
                {
                    if((n/i)*i = n) then
                    {
                        if(c = 0) then
                        {
                            out_int(i);
                            c <- 1;
                        }
                        else
                        {
                            out_string(" * ");
                            out_int(i);
                        }
                        fi;
                        n <- n/i;
                    }
                    else
                    	i <- i+1
                    fi;
                }
                else
                	i<-i+1
                fi;
            }
            pool;
            out_string("\n");

        }
    };

    prime(n : Int): Bool{
      let counter : Bool <- true,
        	i : Int <- 2
        in
        {
            while (i < n) loop
            	{
                    if ((n/i)*i = n) then
                    {
                        counter <- false;
                    	i <- n;
                    }
                    else
                    	i <- i+1
                    fi;
            }
            pool;
           counter;
        }
    };
};
