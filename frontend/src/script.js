function createCommentElement(comment) {
   var li = document.createElement("li");
   var span = document.createElement("span");
   var newContentLi = document.createTextNode(comment.content+ ": ");
   var newContentSpan = document.createTextNode(comment.author);
   li.appendChild(newContentLi);
   span.appendChild(newContentSpan);
   li.appendChild(span);
   return li;
}

function fetchComments(blogId, ul) {
   fetch(`https://api.traefik.me/api/blogs/${blogId}/comments`)
     .then(response => response.json())
     .then(comments => comments.forEach(comment => {
       var li = createCommentElement(comment);
       ul.append(li);
     }))
     .catch((error) => {
       console.error('Error:', error);
     });
}

function createDivElement(blog, includeLink = true) {
   var h2 = document.createElement("h2");
   var p = document.createElement("p");
   var newContenth2 = document.createTextNode(blog.title);
   var newContentp = document.createTextNode(blog.content);
   p.appendChild(newContentp);
   h2.appendChild(newContenth2);
   var article = document.createElement("article");
   article.appendChild(h2);
   article.appendChild(p);
   
   if (includeLink) {
      var a = document.createElement("a");
      a.textContent = "Read more";
      a.href = `/?id=${blog._id}`; // Link to the blog post
      article.appendChild(a);
   }
   
   var h3 = document.createElement("h3");
   h3.textContent = "Comments";
   article.appendChild(h3);
   
   var ul = document.createElement("ul");
   article.appendChild(ul);
   
   document.querySelector("main").append(article);
   
   fetchComments(blog._id, ul);
}

document.addEventListener("DOMContentLoaded", () => {
   // Get the query parameters from the URL
   const urlParams = new URLSearchParams(window.location.search);

   // Get the id parameter
   const id = urlParams.get('id');

   if (id) {
   // If an id parameter is present, fetch the corresponding blog post and its comments
   fetchSinglePost(id);
   } else {
   // If no id parameter is present, fetch all blog posts
      fetch('https://api.traefik.me/api/blogs')
         .then(response => response.json())
         .then(blogs => blogs.forEach(blog => createDivElement(blog, true)))
         .catch((error) => {
            console.error('Error:', error);
      });
      createBlogForm();
   }
});

function fetchSinglePost(blogId) {
   fetch(`https://api.traefik.me/api/blogs/${blogId}`)
     .then(response => response.json())
     .then(blog => createDivElement(blog, false))
     .catch((error) => {
       console.error('Error:', error);
     });
}