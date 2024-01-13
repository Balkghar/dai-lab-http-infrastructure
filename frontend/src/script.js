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
    .then(response => {
      if (!response.ok) { // Check if the response was not ok
        if (response.status === 404) { // Check if the status was 404
          // Handle 404 error
          const noCommentsMessage = document.createElement('li');
          noCommentsMessage.textContent = 'No comments yet.';
          ul.appendChild(noCommentsMessage);
        } else {
          // Handle other errors
          throw new Error(`HTTP error! status: ${response.status}`);
        }
      } else {
        return response.json();
      }
    })
    .then(comments => {
      if (comments) {
        comments.forEach(comment => {
          const commentElement = createCommentElement(comment);
          ul.appendChild(commentElement);
        });
      }
    })
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
  
  fetchComments(blog._id, ul);

  // Return the created article element instead of appending it to the "main" element
  return article;
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
        .then(blogs => blogs.forEach(blog => {
           const blogElement = createDivElement(blog, true);
           document.querySelector("main").appendChild(blogElement);
        }))
        .catch((error) => {
           console.error('Error:', error);
        })
        .finally(() => {
           // Create the blog form after all blogs have been fetched and displayed
           createBlogForm();
        });
  }
});

function fetchSinglePost(blogId) {
  fetch(`https://api.traefik.me/api/blogs/${blogId}`)
    .then(response => response.json())
    .then(blog => {
       const blogElement = createDivElement(blog, false);
       document.querySelector("main").appendChild(blogElement);
    })
    .catch((error) => {
      console.error('Error:', error);
    });
}

function createBlogForm() {
  var form = document.createElement("form");
  var titleInput = document.createElement("input");
  var contentInput = document.createElement("textarea");
  var submitButton = document.createElement("button");

  titleInput.placeholder = "Blog Title";
  titleInput.required = true;
  contentInput.placeholder = "Blog Content";
  contentInput.required = true;
  submitButton.textContent = "Create Blog";

  form.appendChild(titleInput);
  form.appendChild(contentInput);
  form.appendChild(submitButton);

  form.addEventListener("submit", function(event) {
      event.preventDefault();
      createNewBlog(titleInput.value, contentInput.value);
      titleInput.value = '';
      contentInput.value = '';
  });

  document.querySelector("main").appendChild(form);
}

function createNewBlog(title, content) {
  const blogData = {
      title: title,
      content: content
  };

  fetch('https://api.traefik.me/api/blogs', {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json',
      },
      body: JSON.stringify(blogData),
  })
  .then(response => response.json())
  .then(data => {
      console.log('Success:', data);
      const newBlogElement = createDivElement(data, true);
      const form = document.querySelector("form");
      // Insert the new blog before the form
      form.parentNode.insertBefore(newBlogElement, form);
  })
  .catch((error) => {
      console.error('Error:', error);
  });
}