{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell {
  LD_LIBRARY_PATH=pkgs.lib.strings.makeLibraryPath [pkgs.libglvnd pkgs.zlib];
  shellHook = ''
    apply() {
      echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH" > .env
    }
  '';
}