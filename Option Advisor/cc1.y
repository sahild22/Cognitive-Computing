%{
        #include <stdio.h>
        #include <stdlib.h>
        int yylex(void);
%}

%union{
        char c;
        char s[50000];
}

%token <s> HTML
%token <s> SALE
%token <s> TICK
%token <s> DATE
%token <s> RDIV
%token <s> TD
%token <s> TAG
%token <s> SPAN
%token <s> TEXT
%token <s> CDIV
%token <s> TR
%token <s> TAB
%%

list: html
    |list html
;

html: TAG HTML 
    |data HTML		{x++;printf("File = %d\n",x);}					
;

data: TAG
    |TEXT
    |data TAG
    |data TEXT
    |data sale	
    |data tick	
    |data date	
    |data rdiv	
    |data td
    |data SPAN	
;

sale: SALE TEXT   
;

tick: TICK TEXT	
;

date: DATE TEXT		{memcpy(date,$2,20);d = date;insert_back(&t,d);write_y(t);}
;

rdiv: RDIV r_content CDIV
;

r_content: TAG
	 |TAB
	 |r_content TR		
	 |r_content TAB	
         |r_content TEXT	
         |r_content TAG	
         |r_content td
;

td: TD td_content TD
   | td TR 			{insert_back(&t,"\n");write_y(t);line++;if(line % 34 != 0){insert_back(&t,d);write_y(t);}}
;

td_content:			{insert_back(&t," ");write_y(t);}			
	  |TAG			
	  |TEXT			{insert_back(&t,$1);write_y(t);}
          |td_content TAG
          |td_content TEXT	{insert_back(&t,$2);write_y(t);}
;

%%
#include <ctype.h>
FILE *fp;
FILE *cs;
int x=0;
int req=0;
char date[20];
char *d;
//d = date;
typedef struct node{
	char *val;
	struct node *next;
}node_y;

node_y *t = NULL;
int line = 0;

#define IS(a,b) (strstr((a),(b)))


yylex()
{
        int c;
        char *tv=yylval.s;
        int p=0;
        c=getc(stdin);
        if(c==EOF)      
        if(c == '\n')
        {
                c=getc(stdin);
        }
        if(isspace(c))
        {
                while(c!='<')
                {
                        c=getc(stdin);
                }
        }
        if(c=='<')
        {
                while((isalnum(c) || ispunct(c) || isspace(c) || iscntrl(c)) && c!='>')
                {
                        tv[p++]=c; c=getc(stdin);
                }
                tv[p++]=c;
                c=getc(stdin);
                ungetc(c,stdin);
                tv[p]='\0';
		if(tv == "</html>") {req=0;}
           //     printf("tv = %s\n",tv);
                if(IS(tv,"<html")||IS(tv,"</html>"))  {return HTML;}
		if(IS(tv,"id=\"qwidget_lastsale\"")) {return SALE;}
		if(IS(tv,"<div class=\"qwidget-symbol\">"))	{return TICK;}
                if(IS(tv,"<div class=\"OptionsChain-chart borderAll thin\">")) {req=1;return RDIV;}
                if(req==1)
		{
               	        if(IS(tv,"<td") || IS(tv,"</td")) {return TD;}
			if(IS(tv,"<table")||IS(tv,"</table")) {return TAB;}
			if(IS(tv,"</tr")) {return TR;}
			if(strcmp(tv,"</div>")==0) {req=0; return CDIV;}
		}
                if(IS(tv,"<span id=\"qwidget_markettime\">"))   {return DATE;}
                return TAG;
        }
        else
        {
                while((isalnum(c) || ispunct(c) || isspace(c) || iscntrl(c)) && c!='<')
                {
                        tv[p++]=c; c=getc(stdin);
                }
                ungetc(c,stdin);
                tv[p]='\0';
                return TEXT;
        }
}


int yyerror(char *s) { printf(" yyerr: error (%s)\n", s); }

void insert_back(node_y **q,char *c)
{
        node_y *curr,*r;
        if(*q==NULL)
        {
                curr = ( node_y *) malloc (sizeof(node_y));
                curr->val = c;
                curr->next=NULL;
                *q=curr;
        }
        else
        {
		curr = *q;
                while(curr->next!=NULL)
                {
                        curr = curr->next;
                }
                r = (node_y *) malloc (sizeof(node_y));
		r->val = c;
		r->next = NULL;
		curr->next = r;
        }
}


/* void print(node_y *q)
{
        
	while(q!=NULL)
        {
            //    printf("%s",q->val);
                q=q->next;
        }
	printf("\n");
} */

void count(node_y *q)
{
	int c = 0;
	while(q!=NULL)
	{
		q = q->next;
		c++;
	}
	printf("count = %d\n",c);

}

void write_y(node_y *q)
{
	FILE *cs;
	cs = fopen("test3.csv","a");
	while(q->next!=NULL)
	{
	//	printf("%s\n",q->val);
	//	fprintf(cs,"%s",q->val);
		q=q->next;
	}
	if(strcmp(q->val,"\n")==0)
	{
		fprintf(cs,"\"%s\"",q->val);
	}
	else
	{	
	//	printf("%s\n",q->val);
        	fprintf(cs,"\"%s\",",q->val);
	}
	fclose(cs);
}
		


int main(int argc,char **argv)
{
	yyparse();
}


