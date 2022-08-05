FROM kalilinux/kali-rolling:latest
RUN apt-get update
RUN apt update
RUN apt install -y libnfc-bin
RUN apt install -y usbip hwdata usbutils
RUN apt install -y opensc
RUN apt install -y git
RUN apt install -y automake
RUN apt install -y libtool m4
RUN apt install -y pkg-config
RUN apt install -y autoconf-archive
RUN apt install -y flex
RUN apt-get install -y libusb-1.0.0-dev
RUN apt-get install -y pcscd
RUN apt install -y libsystemd-dev
RUN apt install -y udev
RUN apt install -y librust-libudev-dev
RUN apt-get install -y make

RUN git clone https://salsa.debian.org/rousseau/PCSC.git
WORKDIR PCSC
RUN ./bootstrap
RUN ./configure
RUN make
RUN make install
WORKDIR /

RUN git clone --recursive https://salsa.debian.org/rousseau/CCID.git
WORKDIR CCID
RUN ./bootstrap
RUN ./configure
RUN make
RUN make install
WORKDIR /
RUN apt-get install -y nano
RUN echo "enable_pinpad = false" > /etc/opensc.conf
RUN apt-get install -y systemd
RUN service pcscd start
RUN systemctl enable pcscd
RUN apt-get install -y pcsc-tools
RUN apt-get install -y wget

WORKDIR /app
CMD ["bash"]