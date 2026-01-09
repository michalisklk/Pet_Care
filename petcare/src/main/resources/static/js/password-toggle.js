// Toggle visibility για password inputs
document.addEventListener("click", (e) => {
  const btn = e.target.closest(".js-pc-toggle-password");
  if (!btn) return;

  const control = btn.closest(".pc-field__control");
  if (!control) return;

  const input = control.querySelector('input[data-pc-password="true"]');
  if (!input) return;

  const icon = btn.querySelector("img");
  const eye = btn.dataset.eye;
  const eyeOff = btn.dataset.eyeOff;

  const isHidden = input.type === "password";
  input.type = isHidden ? "text" : "password";

  if (icon) {
    icon.src = isHidden ? eye : eyeOff;
  }

  btn.setAttribute("aria-label", isHidden ? "Hide password" : "Show password");
  input.focus();
});
